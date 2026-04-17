(ns clojure.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create
  [{:keys [state
           create!
           dispose!
           render!
           resize!]}]
  (let [state @state]
    (reify ApplicationListener
      (create [_]
        (reset! state (let [[create-fn create-params] create!]
                        (create-fn create-params))))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (let [[render-fn render-params] render!]
          (swap! state render-fn render-params)))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))
