import { Input } from "antd";
import { PlusCircleFilled, SearchOutlined } from "@ant-design/icons";
import { Props } from "next/script";

export default function SearchInput ( props :Props) {
  return (
    <div
      key="SearchOutlined"
      aria-hidden
      style={{
        display: 'flex',
        alignItems: 'center',
        marginInlineEnd: 24,
      }}
      onMouseDown={(e) => {
        e.stopPropagation();
        e.preventDefault();
      }}
    >
      <Input
        style={{
          borderRadius: 4,
          marginInlineEnd: 12,
        }}
        prefix={
          <SearchOutlined
            style={{
              // color: white(),
            }}
          />
        }
        placeholder="搜索方案"
        variant="borderless"
      />

    </div>
  );
};
