(ns com.badlogic.gdx.application-listener
  (:import (com.badlogic.gdx ApplicationListener)))

(defn create
  [{:keys [state-var
           create-pipeline
           dispose
           render-pipeline
           resize]}]
  (let [state @state-var]
    (reify ApplicationListener
      (create [_]
        (reset! state
                (reduce (fn [ctx [f & params]]
                          (apply f ctx params))
                        {}
                        create-pipeline)))

      (dispose [_]
        (doseq [f dispose]
          (f @state)))

      (render [_]
        (swap! state
               (fn [ctx]
                 (reduce (fn [ctx [f & params]]
                           (apply f ctx params))
                         ctx
                         render-pipeline))))

      (resize [_ width height]
        (doseq [f resize]
          (f @state width height)))

      (pause [_])
      (resume [_]))))
