(ns scene2d.actor
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f
  [{:keys [act! draw!]}]
  (actor/proxy-actor {:act! act! :draw! draw!}))
