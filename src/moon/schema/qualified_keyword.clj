(ns moon.schema.qualified-keyword)

(defn malli-form [[_ & params] _schemas]
  (apply vector :qualified-keyword params))
