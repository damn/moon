(ns moon.application.listener
  (:require [clojure.application-listener :refer [application-listener]])
  (:import (com.badlogic.gdx Application)))

(defn f
  [{:keys [state-var
           create-pipeline
           dispose!
           render-pipeline
           resize!]}]
  (application-listener
   (let [state @state-var]
     {:create! (fn [^Application app]
                 (reset! state (reduce (fn [ctx [f & params]]
                                         (apply f ctx params))
                                       {:ctx/audio    (.getAudio app)
                                        :ctx/files    (.getFiles app)
                                        :ctx/graphics (.getGraphics app)
                                        :ctx/input    (.getInput app)}
                                       create-pipeline)))

      :dispose! (fn []
                  (dispose! @state))

      :render! (fn []
                 (swap! state (fn [ctx]
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        ctx
                                        render-pipeline))))

      :resize! (fn [width height]
                 (resize! @state width height))

      :pause! (fn [])

      :resume! (fn [])})))
