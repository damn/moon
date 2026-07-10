(ns clojure.scene2d.utils.click-listener
  (:require [gdl.scenes.scene2d.utils.click-listener :as click-listener]))

(defn create [& args]
  (apply click-listener/create args))
