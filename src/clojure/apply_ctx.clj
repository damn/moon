(ns clojure.apply-ctx
  (:require [clojure.set-ctx :as set-ctx]))

(defn f [stage f]
  (set-ctx/f stage (f (:stage/ctx stage))))
