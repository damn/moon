(ns schema.malli-form.vector)

(defn f [[_ & params] _schemas]
  (apply vector :vector params))
