"use client";

interface ModalMensagemProps {
  mensagem: string;
  onClose: () => void;
}

export default function ModalMensagem({ mensagem, onClose }: ModalMensagemProps) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/60 backdrop-blur-sm z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl w-[400px] text-center">
        <h2 className="text-lg font-semibold text-[#36B0AC] mb-4">Sucesso!</h2>
        <p className="text-gray-700 mb-6">{mensagem}</p>

        <button
          onClick={onClose}
          className="bg-[#36B0AC] hover:bg-[#2c8e8a] text-white px-4 py-2 rounded-md"
        >
          OK
        </button>
      </div>
    </div>
  );
}
