(ns clojure.pack!
  (:require [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn f [& args]
  (apply layout/pack args))
