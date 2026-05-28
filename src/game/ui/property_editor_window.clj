(ns game.ui.property-editor-window
  (:require [clojure.input.keys :as input.keys]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [clojure.scene2d.ui :as ui]
            [moon.ctx :as ctx]
            [moon.db :as db]
            [moon.property :as property]
            [moon.schema :as schema]
            [moon.throwable :as throwable]))

(defmethod actor/create :ui/property-editor-window
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
        widget (schema/create schema property ctx) ; FIXME here no set user object k v ?
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(schema/value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [actor {:keys [ctx/skin
                                               ctx/stage]
                                        :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     stage (actor/stage actor)]
                                 (stage/set-ctx! stage new-ctx))
                               (actor/remove! (actor/find-ancestor actor ui/window?))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (stage/add-actor! stage
                                                   (actor/create {:type :ui/error-window
                                                                  :skin skin
                                                                  :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [ctx (:stage/ctx stage)]
                              (when (ctx/key-just-pressed? ctx input.keys/enter)
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
        rows [[(let [table (actor/create
                            {:type :ui/table
                             :table/cell-defaults {:pad 5}
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
