(ns moon.listener.create
  (:require [moon.ctx :as ctx]
            [qrecord.core :as q]))

(def draw-fns
  (update-vals '{:draw/circle           moon.draw.circle/do!
                 :draw/ellipse          moon.draw.ellipse/do!
                 :draw/filled-circle    moon.draw.filled-circle/do!
                 :draw/filled-rectangle moon.draw.filled-rectangle/do!
                 :draw/grid             moon.draw.grid/do!
                 :draw/line             moon.draw.line/do!
                 :draw/rectangle        moon.draw.rectangle/do!
                 :draw/sector           moon.draw.sector/do!
                 :draw/text             moon.draw.text/do!
                 :draw/texture-region   moon.draw.texture-region/do!
                 :draw/with-line-width  moon.draw.with-line-width/do!}
               requiring-resolve))

(q/defrecord Context []
  ctx/Graphics
  (draw! [ctx draws]
    (doseq [{k 0 :as component} draws
            :when component]
      (apply (get draw-fns k) ctx (rest component)))))

(defn do!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (merge (map->Context {})
                 {
                  :ctx/unit-scale (atom 1)
                  :ctx/factions-iterations {:good 15 :evil 5}
                  :ctx/max-delta 0.04
                  :ctx/minimum-size 0.39
                  :ctx/z-orders [:z-order/on-ground
                                 :z-order/ground
                                 :z-order/flying
                                 :z-order/effect]
                  })
          create-fns))
