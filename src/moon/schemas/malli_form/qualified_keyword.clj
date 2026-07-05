(ns moon.schemas.malli-form.qualified-keyword)

(defn f [[_ & params] _]
  (apply vector :qualified-keyword params))
