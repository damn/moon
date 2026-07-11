(ns moon.stage
  (:require [clojure.set-ctx :as set-ctx]))

(defn apply-ctx! [stage f]
  (set-ctx/f stage (f (:stage/ctx stage))))
