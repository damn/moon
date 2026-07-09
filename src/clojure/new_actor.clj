(ns clojure.new-actor
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [& args]
  (apply actor/new args))
