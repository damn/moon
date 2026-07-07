(ns clojure.apply-ctx
  (:require [clojure.scene2d-stage :refer [set-ctx!]]))

(defn f [stage f]
  (set-ctx! stage (f (:stage/ctx stage))))
