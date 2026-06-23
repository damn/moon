(ns draw-on-world-viewport.draw-entities
  (:require [clojure.order :refer [sort-by-order]]
            [game.constants :refer [show-body-bounds?]]
            [game.ctx.draw :refer [draw!]]
            [game.ctx.draw-component :refer [draw-component]]
            [moon.body :as body]
            [moon.body.draw-rectangle :as draw-rectangle]
            [moon.raycaster.line-of-sight :as line-of-sight?]
            [moon.throwable :as throwable]))

(defn do!
  [{:keys [ctx/active-entities
           ctx/colors
           ctx/player-eid
           ctx/raycaster
           ctx/k->render
           ctx/render-z-order]
    :as ctx}
   render-layers]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (line-of-sight?/f raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                              first
                                              render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try (do
            (when show-body-bounds?
              (draw! ctx (draw-rectangle/f (:entity/body entity)
                                           (if (:body/collides? (:entity/body entity))
                                             (:colors/debug-body-outline-collides colors)
                                             (:colors/debug-body-outline colors)))))
            (doseq [[k v] entity
                    :when (get render-layer k)]
              (draw! ctx (draw-component ctx entity k v))))
           (catch Throwable t
             (draw! ctx (draw-rectangle/f (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
             (throwable/pretty-pst t))))))
