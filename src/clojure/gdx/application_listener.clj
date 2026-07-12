(ns clojure.gdx.application-listener
  (:require [clojure.gdx :as gdx]
            [com.badlogic.gdx.application-listener :as application-listener]))

(defn create
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (application-listener/new
   {:create! (fn []
               (create! (gdx/app)))
    :dispose! (fn []
                (dispose!))
    :render! (fn []
               (render!))
    :resize! (fn [width height]
               (resize! width height))
    :pause! (fn []
              (pause!))
    :resume! (fn []
               (resume!))}))
