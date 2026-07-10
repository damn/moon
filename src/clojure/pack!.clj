(ns clojure.pack!
  (:require [gdl.scenes.scene2d.utils.layout :as layout]))

(defn f [& args]
  (apply layout/pack args))
