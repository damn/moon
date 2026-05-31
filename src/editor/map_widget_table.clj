(ns editor.map-widget-table
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]
            [editor.map-widget-table.component-row :as component-row]
            [editor.map-widget-table.k-label-text :as k-label-text]
            [editor.rebuild :as rebuild]
            [editor.widget :as widget]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]
            [moon.schemas :as schemas]
            [moon.ui.error-window]))

(defn add-component-window
  [{:keys [schemas schema map-widget-table skin]}]
  (let [window (window/create
                {:title "Choose"
                 :skin skin
                 :window/close-button? skin
                 :window/modal? true
                 :table/cell-defaults {:pad 5}})
        remaining-ks (sort (remove (set (keys (widget/value schema map-widget-table schemas)))
                                   (schemas/map-keys schemas schema)))]
    (table/add-rows!
     window
     (for [k remaining-ks]
       [{:actor (text-button/create
                 {:skin skin
                  :text (name k)
                  :actor/listeners {:listener/change
                                    (fn [event _actor]
                                      (actor/remove! window)
                                      (let [ctx (:stage/ctx (event/stage event))]
                                        (table/add-rows! map-widget-table [(component-row/create
                                                                            {:skin skin
                                                                             :editor-widget (widget/build ctx
                                                                                                          (get schemas k)
                                                                                                          k
                                                                                                          (schemas/default-value schemas k))
                                                                             :k k
                                                                             :display-remove-component-button? (schemas/optional? schemas schema k)
                                                                             :table map-widget-table})])
                                        (rebuild/f! ctx)))}})}]))
    (widget-group/pack! window)
    window))

(defn- horiz-sep [colspan]
  (fn []
    [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
      :pad-top 2
      :pad-bottom 2
      :colspan colspan
      :fill-x? true
      :expand-x? true}]))

(defn- interpose-f [f coll] ; TODO use interpose?
  (drop 1 (interleave (repeatedly f) coll)))

(defn create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}
                :actor/name "moon.db.schema.map.ui.widget"})
        colspan 3
        component-rows (interpose-f (horiz-sep colspan)
                                    (map (fn [k]
                                           (component-row/create
                                            {:skin skin
                                             :editor-widget (k->widget k)
                                             :k k
                                             :display-remove-component-button? (k->optional? k)
                                             :table table}))
                                         ks-sorted))]
    (table/add-rows!
     table
     (concat [(when opt?
                [{:actor (text-button/create
                          {:text "Add component"
                           :skin skin
                           :actor/listeners {:listener/change
                                             (fn [event actor]
                                               (let [{:keys [ctx/db
                                                             ctx/stage
                                                             ctx/skin]} (:stage/ctx (event/stage event))]
                                                 (stage/add-actor!
                                                  stage
                                                  (add-component-window
                                                   {:skin skin
                                                    :schemas (:db/schemas db)
                                                    :schema schema
                                                    :map-widget-table table}))))}})
                  :colspan colspan}])]
             [(when opt?
                [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))
