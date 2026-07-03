(ns editor.widget.one-to-many.add-one-to-many-rows
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.event.get-stage :as get-stage]
            [clojure.gdx.group.clear-children :as clear-children]
            [clojure.gdx.image.new :as new-image]
            [clojure.gdx.layout.pack :as pack]
            [clojure.gdx.stage.add-actor :as add-actor]
            [clojure.gdx.window.instance? :as window?]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.utils.change-listener :as change-listener]
            [moon.db.get-raw :refer [get-raw]]
            [moon.property.tooltip :as tooltip]
            [moon.property.image :as property-image]
            [moon.textures :as textures]))

(defn add-one-to-many-rows
  [{:keys [ctx/db
           ctx/skin
           ctx/textures]}
   table
   property-type
   property-ids]
  (let [redo-rows (fn [ctx property-ids]
                    (clear-children/f table)
                    (add-one-to-many-rows ctx table property-type property-ids)
                    (pack/f (find-ancestor table window?/f)))]
    (add-rows!
     table
     [[{:actor (doto (text-button/create
                      {:text "+"
                       :skin skin})
                 (add-listener/f (change-listener/create
                                  (fn [event _actor]
                                    (let [{:keys [ctx/db
                                                  ctx/skin
                                                  ctx/stage
                                                  ctx/textures
                                                  ctx/property-overview-window]
                                           :as ctx} (:stage/ctx (get-stage/f event))]
                                      (add-actor/f
                                       stage
                                       (property-overview-window
                                        {:db db
                                         :textures textures
                                         :skin skin
                                         :property-type property-type
                                         :clicked-id-fn (fn [actor id ctx]
                                                          (remove/f (find-ancestor actor window?/f))
                                                          (redo-rows ctx (conj property-ids id)))})))))))}]
      (for [property-id property-ids]
        (let [property (get-raw db property-id)]
          {:actor (doto (new-image/f (textures/texture-region textures (property-image/f property)))
                    (add-listener/f (text-tooltip/create (tooltip/f property) skin))
                    (set-user-object/f property-id))}))
      (for [id property-ids]
        {:actor (doto (text-button/create
                       {:text "-"
                        :skin skin})
                  (add-listener/f (change-listener/create
                                   (fn [event _actor]
                                     (redo-rows (:stage/ctx (get-stage/f event))
                                                (disj property-ids id))))))})])))
