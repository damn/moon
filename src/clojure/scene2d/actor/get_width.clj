(ns clojure.scene2d.actor.get-width
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [& args]
  (apply actor/getWidth args))
