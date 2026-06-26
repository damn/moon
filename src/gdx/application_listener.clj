(ns gdx.application-listener
  (:require [com.badlogic.gdx.application-listener :as application-listener]))

(defn application-listener
  [{:keys [create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (application-listener/reify-application-listener
   {:create! create!
    :dispose! dispose!
    :render! render!
    :resize! resize!
    :pause! pause!
    :resume! resume!}))
