(ns schema.malli-form.enum)

(defn f [[_ & params] _schemas]
  (apply vector :enum params))
