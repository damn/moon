(ns moon.schemas.malli-form.vector)

(defn f [[_ & params] _]
  (apply vector :vector params))
