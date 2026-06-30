(ns scene2d.actor.is-button
  (:require [clojure.gdx :as gdx]))

(defn f [actor]
  (or (gdx/button-class? actor)
      (when-let [parent (gdx/get-parent actor)]
        (gdx/button-class? parent))))
