import "../globals.css";
import Navbar from "@/app/Components/Navbar/page";

export const metadata = {
  title: "Sistema - Artesano Brasil",
  description: "Painel administrativo do sistema Artesano Brasil",
};

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex flex-col bg-white">
      {/* Navbar fixa em todas as páginas internas do sistema */}
      <Navbar />

      {/* Conteúdo dinâmico de cada rota */}
      <main>
        {children}
      </main>
    </div>
  );
}
