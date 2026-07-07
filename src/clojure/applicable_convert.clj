(ns clojure.applicable-convert
  (:require [clojure.moon-faction :as faction]))

(defn f
  [_ {:keys [effect/source effect/target]}]
  (and target
       (= (:entity/faction @target)
          (faction/enemy (:entity/faction @source)))))
