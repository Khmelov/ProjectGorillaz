select now()::timestamp(6);

SELECT '{"bar": "baz", "balance": 7.77, "active": false}'::jsonb->>'balance'  b;