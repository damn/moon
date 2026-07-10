(ns clojure.scene2d.utils.change-listener
  (:require [gdl.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create [& args]
  (apply change-listener/create args))
