

DO $$
BEGIN

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'tb_categoria' AND column_name = 'categoria_pai_id'
    ) THEN
        ALTER TABLE tb_categoria
        ADD COLUMN categoria_pai_id BIGINT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_categoria_pai'
    ) THEN
        ALTER TABLE tb_categoria
        ADD CONSTRAINT fk_categoria_pai
        FOREIGN KEY (categoria_pai_id)
        REFERENCES tb_categoria(id)
        ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'tb_categoria' AND indexname = 'idx_categoria_pai_id'
    ) THEN
        CREATE INDEX idx_categoria_pai_id ON tb_categoria(categoria_pai_id);
    END IF;
END $$;


DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'tb_produto' AND column_name = 'subcategoria_id'
    ) THEN
        ALTER TABLE tb_produto
        ADD COLUMN subcategoria_id BIGINT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_produto_subcategoria'
    ) THEN
        ALTER TABLE tb_produto
        ADD CONSTRAINT fk_produto_subcategoria
        FOREIGN KEY (subcategoria_id)
        REFERENCES tb_categoria(id)
        ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'tb_produto' AND indexname = 'idx_produto_subcategoria_id'
    ) THEN
        CREATE INDEX idx_produto_subcategoria_id ON tb_produto(subcategoria_id);
    END IF;
END $$;


UPDATE tb_categoria
SET atualizado_em = NOW()
WHERE atualizado_em IS NULL;

UPDATE tb_produto
SET atualizado_em = NOW()
WHERE atualizado_em IS NULL;


UPDATE tb_categoria
SET categoria_pai_id = NULL
WHERE categoria_pai_id = id;
