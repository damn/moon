(ns editor.window
  (:require [gdx.application :as app]
            [gdx.input :as input]
            [com.badlogic.gdx.scenes.scene2d.actor.get-stage :refer [get-stage]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-name :refer [set-name!]]
            [com.badlogic.gdx.scenes.scene2d.actor.add-listener :refer [add-listener!]]
            [com.badlogic.gdx.scenes.scene2d.group.add-actor :refer [add-actors!]]
            [com.badlogic.gdx.scenes.scene2d.event.get-stage :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.table.scroll-pane-cell :as scroll-pane-cell]
            [editor.widget :as widget]
            [editor.window.with-window-close :as with-window-close]
            [com.badlogic.gdx.scenes.scene2d.actor.create :as actor]
            [gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [moon.db.delete :refer [delete!]]
            [moon.db.update :refer [update!]]
            [moon.property.type :refer [property->type]]))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property->type property))
        widget (widget/create schema property ctx) ; FIXME here no set user object k v ? (widget/build)
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget/value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close/f (fn [db]
                                                 (delete! db property-id)))
        clicked-save-fn (with-window-close/f (fn [db]
                                               (update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create
                                          {:text "Save [LIGHT_GRAY](ENTER)[]"
                                           :skin skin})
                                     (add-listener! (change-listener/create
                                                     (fn [event actor]
                                                       (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (add-listener! (change-listener/create
                                                     (fn [event actor]
                                                       (clicked-delete-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}]]]
    (doto (window/create
           {:title "[SKY]Property[]"
            :skin skin
            :window/close-button? skin
            :window/modal? true
            :table/cell-defaults {:pad 5}
            :table/rows [[(scroll-pane-cell/create
                           (table/create {:table/cell-defaults {:pad 5}
                                          :table/rows scroll-pane-rows})
                           skin
                           scroll-pane-height
                           50)]]})
      (add-actors! [(actor/create
                     {:act! (fn [this delta]
                              (when-let [stage (get-stage this)]
                                (let [ctx (:stage/ctx stage)]
                                  (when (input/key-just-pressed? (app/input (:ctx/app ctx)) :input.keys/enter)
                                    (clicked-save-fn this ctx)))))})])
      (set-name! "moon.ui.editor.window"))))
