import tw from "tailwind-styled-components";

export const Wrapper = tw.div`
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
`;

export const HeaderBar = tw.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`;

export const Breadcrumb = tw.h2`
  font-size: 16px;
  font-weight: bold;
  color: #009c9c;
`;
