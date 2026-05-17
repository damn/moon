(ns moon.listener)

(defn create
  [{:keys [state-var
           create
           dispose
           render
           resize]}]
  (let [state @state-var]
    {:create! (fn []
                (reset! state
                        (reduce (fn [ctx [f & params]]
                                  (apply f ctx params))
                                {}
                                create)))
     :dispose! (fn []
                 (doseq [f dispose]
                   (f @state)))
     :render! (fn []
                (swap! state
                       (fn [ctx]
                         (reduce (fn [ctx [f & params]]
                                   (apply f ctx params))
                                 ctx
                                 render))))
     :resize! (fn [width height]
                (doseq [f resize]
                  (f @state width height)))
     :pause! (fn [])
     :resume! (fn [])}))
