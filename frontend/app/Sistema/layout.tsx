import Navbar from "@/app/Components/Navbar/page";

export const metadata = {
  title: "Sistema - Artesano Brasil",
  description: "Painel administrativo do sistema Artesano Brasil",
};

export default function SistemaLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Navbar />
      <main className="grow mt-16">
        {children}
      </main>
    </div>
  );
}
