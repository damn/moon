(ns clojure.widget-value-default
  (:require
            [clojure.get-user-object]))

(defn f
  [_  widget _schemas]
  ((clojure.get-user-object/f widget) 1))
