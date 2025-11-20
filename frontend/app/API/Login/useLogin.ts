"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { loginUsuario } from "./apiServiceLogin";
import { setAuthToken } from "./auth";

export function useLogin() {
  const router = useRouter();
  const [form, setForm] = useState({ email: "", senha: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!form.email || !form.senha) {
      alert("Preencha todos os campos.");
      return;
    }

    try {
      setLoading(true);
      const result = await loginUsuario(form);

      if (result.token) {
        setAuthToken(result.token);
        router.push("/Sistema");
      } else {
        throw new Error("Token não recebido do servidor.");
      }
    } catch (error: unknown) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError("Erro inesperado.");
      }
    } finally {
      setLoading(false);
    }
  };

  return {
    form,
    loading,
    error,
    handleChange,
    handleSubmit,
  };
}
