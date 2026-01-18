(ns moon.start.create-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn step
  [ctx
   {:keys [state
           create!
           dispose!
           render!
           resize!]}]
  (assoc ctx :app/listener
         (let [state @state
               [create-fn create-params] create!
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
