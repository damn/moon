(ns clojure.scene2d.actor.stage-to-local-coordinates
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [& args]
  (apply actor/stage-to-local-coordinates args))
