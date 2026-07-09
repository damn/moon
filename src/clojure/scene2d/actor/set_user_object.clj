(ns clojure.scene2d.actor.set-user-object
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [& args]
  (apply actor/set-user-object args))
