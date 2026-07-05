(ns moon.schemas.malli-form.number)

(defn f [[_ predicate] _]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))
