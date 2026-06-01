(ns editor.window
  (:require [clojure.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.keys :as input.keys]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.scroll-pane :as scroll-pane]
            [editor.widget :as widget]
            [editor.window.with-window-close :as with-window-close]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.db :as db]
            [moon.property :as property]))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        ; build for get-widget-value
        ; or find a way to find the widget from the context @ save button
        ; should be possible
        widget (widget/create schema property ctx) ; FIXME here no set user object k v ? (widget/build)
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget/value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close/f (fn [db]
                                                 (db/delete! db property-id)))
        clicked-save-fn (with-window-close/f (fn [db]
                                               (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [ctx (:stage/ctx stage)]
                              (when (input/key-just-pressed? (app/input (:ctx/app ctx)) input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button {:text "Save [LIGHT_GRAY](ENTER)[]"
                     :skin skin
                     :actor/listeners [[:listener/change
                                        (fn [event actor]
                                          (clicked-save-fn actor (:stage/ctx (event/stage event))))]]}
        delete-button {:text "Delete"
                       :skin skin
                       :actor/listeners [[:listener/change
                                          (fn [event actor]
                                            (clicked-delete-fn actor (:stage/ctx (event/stage event))))]]}
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (text-button/create save-button) :center? true}
                           {:actor (text-button/create delete-button) :center? true}]]
        rows [[(let [table (table/create
                            {:table/cell-defaults {:pad 5}
                             :table/rows scroll-pane-rows})]
                 {:actor (scroll-pane/create
                          {:actor table
                           :skin skin})
                  :width  (+ (actor/width table) 50)
                  :height (min (- scroll-pane-height 50)
                               (actor/height table))})]]]
    (window/create
     {:title "[SKY]Property[]"
      :skin skin
      :window/close-button? skin
      :window/modal? true
      :table/cell-defaults {:pad 5}
      :table/rows rows
      :group/actors actors
      :actor/name "moon.ui.editor.window"})))
