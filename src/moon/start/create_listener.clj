(ns moon.start.create-listener
  (:require [moon.application :refer [state]])
  (:import (com.badlogic.gdx ApplicationListener)))

(defn step
  [ctx
   {:keys [create!
           dispose!
           render!
           resize!]}]
  (assoc ctx :app/listener
         (let [[create-fn create-params] create!
               [render-fn render-params] render!]
           (reify ApplicationListener
             (create [_]
               (reset! state (create-fn create-params)))

             (dispose [_]
               (dispose! @state))

             (render [_]
               (swap! state render-fn render-params))

             (resize [_ width height]
               (resize! @state width height))

             (pause [_])

             (resume [_])))))
