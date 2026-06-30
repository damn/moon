(ns scene2d.actor
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [act! draw!]}]
  (gdx/actor {:act! act! :draw! draw!}))
