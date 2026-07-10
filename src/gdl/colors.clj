(ns gdl.colors
  (:require [com.badlogic.gdx.graphics.colors :as colors]))

(defn put! [& args]
  (apply colors/put args))
