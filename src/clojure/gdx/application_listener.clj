(ns clojure.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)))

(defn create
  [{:keys [state-var
           create-pipeline
           dispose!
           render!
           render-params
           resize!]}]
  (let [state @state-var]
    (reify ApplicationListener
      (create [_]
        (reset! state (reduce (fn [ctx [f & params]]
                                (apply f ctx params))
                              {:ctx/app Gdx/app}
                              create-pipeline)))

      (dispose [_]
        (dispose! @state))

      (render [_]
        (swap! state render! render-params))

      (resize [_ width height]
        (resize! @state width height))

      (pause [_])

      (resume [_]))))
