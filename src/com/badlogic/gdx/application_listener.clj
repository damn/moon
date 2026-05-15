(ns com.badlogic.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)))

(defn create
  [{:keys [state-var
           create
           dispose
           render
           resize]}]
  (let [state @state-var]
    (reify ApplicationListener
      (create [_]
        (reset! state
                (reduce (fn [ctx [f & params]]
                          (apply f ctx params))
                        {:ctx/app Gdx/app}
                        create)))

      (dispose [_]
        (doseq [f dispose]
          (f @state)))

      (render [_]
        (swap! state
               (fn [ctx]
                 (reduce (fn [ctx [f & params]]
                           (apply f ctx params))
                         ctx
                         render))))

      (resize [_ width height]
        (doseq [f resize]
          (f @state width height)))

      (pause [_])
      (resume [_]))))
