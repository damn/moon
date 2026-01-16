(ns moon.application
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)))

(def state (atom nil))

(defn listener
  [{:keys [create!
           dispose!
           render!
           resize!]}]
  (let [[create-fn create-params] create!
        [render-fn render-params] render!]
    (reify ApplicationListener
      (create [_]
        (reset! state (create-fn Gdx/app create-params)))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render-fn render-params))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))
