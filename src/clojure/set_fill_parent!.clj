(ns clojure.set-fill-parent!
  (:require [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn f [& args]
  (apply layout/set-fill-parent args))
