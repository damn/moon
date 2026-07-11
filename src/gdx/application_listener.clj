(ns gdx.application-listener
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.gdx :as gdx]))

(defn new [{:keys [create! dispose! render! resize!]}]
  (listener/new
   {:create! (fn [] (create! (gdx/app)))
    :dispose! dispose!
    :render! render!
    :resize! resize!
    :pause! (fn [])
    :resume! (fn [])}))
