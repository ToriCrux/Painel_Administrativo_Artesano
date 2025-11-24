"use client";

import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { useCallback } from "react";

/**
 * Hook responsável por gerar um PDF a partir de um elemento DOM
 * Garante compatibilidade mesmo sem imagens (apenas texto/números)
 */
export const useGerarPDF = () => {
  const gerarPDF = useCallback(async (elementId: string, nomeArquivo: string) => {
    const elemento = document.getElementById(elementId);

    if (!elemento) {
      console.error("❌ Elemento não encontrado para geração do PDF:", elementId);
      return;
    }

    try {
      // Captura segura do conteúdo
      const canvas = await html2canvas(elemento, {
        scale: 2,
        useCORS: true,
        allowTaint: true,
        backgroundColor: "#FFFFFF",
      });

      // Verificação de conteúdo válido
      const imgData = canvas.toDataURL("image/png");
      if (!imgData.startsWith("data:image/png")) {
        throw new Error("❌ Conteúdo do canvas inválido (sem PNG válido).");
      }

      // Criação do PDF
      const pdf = new jsPDF("p", "mm", "a4");
      const width = pdf.internal.pageSize.getWidth();
      const height = (canvas.height * width) / canvas.width;

      pdf.addImage(imgData, "PNG", 0, 0, width, height);
      pdf.save(`${nomeArquivo}.pdf`);
    } catch (error) {
      console.error("❌ Erro ao gerar PDF:", error);
    }
  }, []);

  return { gerarPDF };
};
