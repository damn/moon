(ns moon.application.render.draw-on-world-viewport.geom-test)

(comment
 (require '[moon.world.grid :as grid])
 (require '[moon.math.geom :as geom])

 (defn geom-test
   [{:keys [ctx/graphics
            ctx/world]}]
   (let [position (:graphics/world-mouse-position graphics)
         radius 0.8
         circle {:position position
                 :radius radius}]
     (conj (cons [:draw/circle position radius [1 0 0 0.5]]
                 (for [[x y] (map #(:position @%) (grid/circle->cells (:world/grid world) circle))]
                   [:draw/rectangle x y 1 1 [1 0 0 0.5]]))
           (let [{:keys [x y width height]} (geom/circle->outer-rectangle circle)]
             [:draw/rectangle x y width height [0 0 1 1]]))))

 )
