(ns schema.malli-form.qualified-keyword)

(defn f [[_ & params] _schemas]
  (apply vector :qualified-keyword params))
