(ns clojure.malli-form-enum)

(defn f [[_ & params] _]
  (apply vector :enum params))
