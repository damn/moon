(ns clojure.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create
  [{:keys [
           state-var
           create!
           create-params
           dispose!
           render!
           render-params
           resize!
           ]}]
  (let [state @state-var]
    (reify ApplicationListener
      (create [_]
        (reset! state (create! create-params)))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render! render-params))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))
