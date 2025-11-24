// middleware.ts
import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const token = request.cookies.get("token")?.value;

  // Bloqueia qualquer acesso a /Sistema sem token
  if (!token && request.nextUrl.pathname.startsWith("/Sistema")) {
    return NextResponse.redirect(new URL("/Login", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/Sistema/:path*", "/Sistema"], // protege /Sistema e subrotas
};
