<details>

*** ___COMMENTS & FIXMES & TODOS &  DEAD CODE !!!! & TESTS MISSING !!! *****

What to do => DO THE TODOS!

;\|#_\|comment

* test levelgen/game all lvls.
* no WARN ON REFLECTION
* ^:private/defn-
* transitives
* imports
* count locs
* hiera
* hiera cluster 1

<summary>
sh count_locs.sh
</summary>

       4 src//com/badlogic/gdx/audio.clj
       4 src//com/badlogic/gdx/audio/sound.clj
       4 src//com/badlogic/gdx/files.clj
       4 src//com/badlogic/gdx/graphics/pixmap/format.clj
       4 src//com/badlogic/gdx/graphics/texture/texture_filter.clj
       4 src//com/badlogic/gdx/scenes/scene2d/touchable.clj
       4 src//com/badlogic/gdx/utils/disposable.clj
       4 src//moon/application/create/world_unit_scale.clj
       4 src//moon/application/render/dissoc_interaction_state.clj
       4 src//moon/audio.clj
       4 src//moon/draws.clj
       4 src//moon/files.clj
       4 src//moon/txs.clj
       4 src//moon/ui/image.clj
       4 src//moon/ui/label.clj
       4 src//moon/ui/select_box.clj
       4 src//moon/ui/text_field.clj
       5 src//com/badlogic/gdx/backends/lwjgl3/lwjgl3_application.clj
       5 src//com/badlogic/gdx/graphics/colors.clj
       5 src//com/badlogic/gdx/graphics/g2d/sprite_batch.clj
       5 src//com/badlogic/gdx/input/buttons.clj
       5 src//com/badlogic/gdx/maps/tiled/tmx_map_loader.clj
       5 src//com/badlogic/gdx/math/circle.clj
       5 src//com/badlogic/gdx/scenes/scene2d/event.clj
       5 src//com/badlogic/gdx/scenes/scene2d/ui/stack.clj
       5 src//com/badlogic/gdx/scenes/scene2d/ui/tooltip_manager.clj
       5 src//com/badlogic/gdx/scenes/scene2d/utils/drawable.clj
       5 src//moon/application/create/batch.clj
       5 src//moon/application/create/require_ui_impls.clj
       5 src//moon/edn.clj
       5 src//moon/ui/widget_group.clj
       6 src//com/badlogic/gdx/scenes/scene2d/ui/image_button.clj
       6 src//com/badlogic/gdx/scenes/scene2d/ui/text_tooltip.clj
       6 src//moon/application/create/add_stage_actors/action_bar.clj
       6 src//moon/application/create/render_z_order.clj
       6 src//moon/application/create/tooltip_config.clj
       6 src//moon/application/render/clear_screen.clj
       6 src//moon/faction.clj
       6 src//moon/string.clj
       6 src//moon/ui/table.clj
       7 src//clojure/gdx/scene2d/ui/scroll_pane.clj
       7 src//clojure/gdx/scene2d/ui/widget.clj
       7 src//com/badlogic/gdx/application.clj
       7 src//com/badlogic/gdx/math/intersector.clj
       7 src//com/badlogic/gdx/math/vector3.clj
       7 src//com/badlogic/gdx/scenes/scene2d/ui/horizontal_group.clj
       7 src//com/badlogic/gdx/scenes/scene2d/ui/text_button.clj
       7 src//com/badlogic/gdx/scenes/scene2d/utils/change_listener.clj
       7 src//com/badlogic/gdx/scenes/scene2d/utils/click_listener.clj
       7 src//moon/application/create/max_speed.clj
       7 src//moon/application/create/record.clj
       8 src//clojure/gdx/scene2d/ui/horizontal_group.clj
       8 src//clojure/gdx/scene2d/ui/text_button.clj
       8 src//com/badlogic/gdx/scenes/scene2d/ui/scroll_pane.clj
       8 src//com/badlogic/gdx/scenes/scene2d/ui/widget.clj
       8 src//com/badlogic/gdx/scenes/scene2d/ui/widget_group.clj
       8 src//com/badlogic/gdx/utils/align.clj
       8 src//moon/application/create/explored_tile_corners.clj
       8 src//moon/application/create/gdx_colors.clj
       8 src//moon/application/render/get_stage_ctx.clj
       8 src//moon/application/render/set_camera.clj
       8 src//moon/graphics.clj
       8 src//moon/ui/group.clj
       8 src//moon/vector.clj
       8 src//moon/world_fns/tmx.clj
       9 src//clojure/gdx/scene2d/ui/stack.clj
       9 src//com/badlogic/gdx/math/vector2.clj
       9 src//com/badlogic/gdx/scenes/scene2d/ui/check_box.clj
       9 src//com/badlogic/gdx/scenes/scene2d/ui/label.clj
       9 src//com/badlogic/gdx/scenes/scene2d/ui/skin.clj
       9 src//com/badlogic/gdx/scenes/scene2d/utils/texture_region_drawable.clj
       9 src//moon/application/create/add_stage_actors/windows.clj
       9 src//moon/application/create/shape_drawer.clj
       9 src//moon/application/render/active_entities.clj
       9 src//moon/application/render/stage.clj
       9 src//moon/throwable.clj
      10 src//clojure/math/circle.clj
      10 src//com/badlogic/gdx/files/file_handle.clj
      10 src//com/badlogic/gdx/graphics/gl20.clj
      10 src//com/badlogic/gdx/scenes/scene2d/ui/text_field.clj
      10 src//moon/application/create/add_stage_actors.clj
      10 src//moon/application/create/content_grid.clj
      10 src//moon/application/render/if_not_paused/update_time.clj
      10 src//moon/camera.clj
      10 src//moon/raycaster.clj
      10 src//moon/textures.clj
      11 src//clojure/math/raycaster.clj
      11 src//com/badlogic/gdx/gdx.clj
      11 src//com/badlogic/gdx/graphics/color.clj
      11 src//com/badlogic/gdx/graphics/g2d/bitmap_font/data.clj
      11 src//com/badlogic/gdx/graphics/g2d/freetype/freetype_font_generator.clj
      11 src//com/badlogic/gdx/graphics/texture.clj
      11 src//moon/application/create/audio.clj
      11 src//moon/application/resize.clj
      11 src//moon/creature_tiles.clj
      11 src//moon/stage.clj
      12 src//clojure/gdx/scene2d/ui/select_box.clj
      12 src//com/badlogic/gdx/graphics/g2d/freetype/freetype_font_generator/parameters.clj
      12 src//com/badlogic/gdx/math/rectangle.clj
      12 src//com/badlogic/gdx/scenes/scene2d/ui/select_box.clj
      12 src//moon/application/create/shape_drawer_texture.clj
      12 src//moon/application/render/if_not_paused.clj
      13 src//clojure/gdx/scene2d/ui/label.clj
      13 src//clojure/gdx/scene2d/ui/text_field.clj
      13 src//moon/application/render/draw_on_world_viewport/highlight_mouseover_tile.clj
      13 src//moon/tx/audiovisual.clj
      14 src//clojure/gdx/scene2d/ui/image.clj
      14 src//com/badlogic/gdx/backends/lwjgl3/lwjgl3_application_configuration.clj
      14 src//moon/application/create/grid.clj
      14 src//moon/application/create/raycaster.clj
      14 src//moon/application/create/skin.clj
      14 src//moon/application/render/update_mouse.clj
      14 src//moon/scratch.clj
      15 src//clojure/gdx/scene2d/ui/widget_group.clj
      15 src//com/badlogic/gdx/input/keys.clj
      15 src//com/badlogic/gdx/scenes/scene2d/ui/table.clj
      15 src//com/badlogic/gdx/scenes/scene2d/ui/window.clj
      16 src//clojure/config.clj
      16 src//clojure/gdx/utils/viewport.clj
      16 src//com/badlogic/gdx/graphics.clj
      16 src//moon/application/create/cursors.clj
      16 src//moon/application/render/draw_on_world_viewport/draw_tile_grid.clj
      16 src//moon/application/render/if_not_paused/update_potential_fields.clj
      16 src//moon/position.clj
      17 src//clojure/gdx/scene2d/ui/image_button.clj
      17 src//com/badlogic/gdx/graphics/pixmap.clj
      17 src//com/badlogic/gdx/input.clj
      17 src//com/badlogic/gdx/scenes/scene2d/group.clj
      17 src//moon/map.clj
      17 src//moon/player_item_on_cursor.clj
      18 src//com/badlogic/gdx/scenes/scene2d/ui/button_group.clj
      18 src//moon/application/create/db.clj
      18 src//moon/application/create/spawn_enemies.clj
      18 src//moon/tiled_map/renderer.clj
      18 src//moon/val_max.clj
      19 src//clojure/gdx/scene2d/group.clj
      19 src//com/badlogic/gdx/graphics/g2d/texture_region.clj
      19 src//com/badlogic/gdx/scenes/scene2d/ui/image.clj
      19 src//moon/create_assets_edn.clj
      20 src//clojure/math/rectangle.clj
      20 src//moon/application/dispose.clj
      20 src//moon/skill.clj
      21 src//com/badlogic/gdx/graphics/g2d/bitmap_font.clj
      21 src//com/badlogic/gdx/utils/viewport/fit_viewport.clj
      22 src//com/badlogic/gdx/scenes/scene2d/ui/cell.clj
      22 src//moon/application/create/spawn_player.clj
      22 src//moon/application/create/unorganised.clj
      22 src//moon/application/render/remove_destroyed_entities.clj
      22 src//moon/timer.clj
      23 src//clojure/animation.clj
      23 src//moon/ops.clj
      23 src//moon/ui/error_window.clj
      24 src//clojure/gdx/scene2d/ui/window.clj
      24 src//moon/application/create/add_stage_actors/windows/info.clj
      25 src//moon/number.clj
      26 src//moon/application/create/controls.clj
      26 src//moon/application/render/check_debug_viewer.clj
      26 src//moon/application/render/draw_on_world_viewport/draw_cell_debug.clj
      26 src//moon/input.clj
      27 src//moon/application/create/tiled_map.clj
      27 src//moon/property.clj
      28 src//com/badlogic/gdx/application_listener.clj
      28 src//moon/application/create/textures.clj
      29 src//com/badlogic/gdx/graphics/orthographic_camera.clj
      29 src//com/badlogic/gdx/scenes/scene2d/ctx_stage.clj
      29 src//moon/application/render/draw_on_world_viewport.clj
      29 src//moon/order.clj
      30 src//moon/application/create/add_stage_actors/player_message.clj
      30 src//moon/application/create/add_stage_actors/player_state_draw.clj
      30 src//moon/ui/actor.clj
      31 src//com/badlogic/gdx/graphics/g2d/batch.clj
      31 src//moon/migrations/add_obligatory_mana.clj
      33 src//moon/tx/state_exit.clj
      34 src//moon/application/render/update_mouseover_eid.clj
      34 src//moon/body.clj
      34 src//moon/ui_actors/windows/info.clj
      35 src//moon/application/render/window_camera_controls.clj
      35 src//space/earlygrey/shapedrawer/shape_drawer.clj
      36 src//moon/application/create/default_font.clj
      37 src//moon/application/create/stage.clj
      37 src//moon/entity.clj
      40 src//moon/application/create/world_viewport.clj
      40 src//moon/tx/state_enter.clj
      41 src//moon/application/render/assoc_paused.clj
      42 src//moon/application/create/app.clj
      44 src//moon/content_grid.clj
      45 src//clojure/gdx/scene2d/ui/table.clj
      49 src//moon/start.clj
      50 src//moon/cell.clj
      52 src//moon/application/render/validate.clj
      55 src//moon/application/create/add_stage_actors/hp_mana_bar.clj
      56 src//moon/ui_actors/action_bar.clj
      57 src//moon/application/create/add_stage_actors/windows/inventory.clj
      58 src//moon/application/create/ctx_colors.clj
      60 src//clojure/graphics/orthographic_camera.clj
      61 src//moon/inventory.clj
      62 src//moon/rename.clj
      63 src//moon/db.clj
      66 src//moon/stats.clj
      68 src//moon/dev_loop.clj
      71 src//clojure/math/vector2.clj
      72 src//com/badlogic/gdx/scenes/scene2d/actor.clj
      73 src//moon/application/render/assoc_interaction_state.clj
      74 src//moon/ui/data_viewer_window.clj
      78 src//moon/application/render/draw_tiled_map.clj
      78 src//moon/application/render/set_cursor.clj
      82 src//moon/nads.clj
      84 src//moon/hiera.clj
      86 src//clojure/rand.clj
      88 src//moon/migrations.clj
      88 src//moon/ui/property_overview_window.clj
      89 src//moon/ui/property_editor_window.clj
      89 src//moon/ui_actors/dev_menu.clj
      94 src//moon/state.clj
     100 src//moon/application/render/handle_input.clj
     101 src//moon/schemas.clj
     102 src//moon/application/create/add_stage_actors/dev_menu.clj
     106 src//moon/caves.clj
     108 src//clojure/gdx/scene2d/actor.clj
     108 src//moon/malli.clj
     125 src//moon/application/create/impl_draws.clj
     150 src//moon/tiled_map.clj
     152 src//moon/world_fns/uf_caves.clj
     168 src//moon/tx/spawn_entity.clj
     175 src//moon/ui_actors/windows/inventory.clj
     230 src//moon/application/render/draw_on_world_viewport/draw_entities.clj
     275 src//moon/application/render/if_not_paused/tick_entities.clj
     295 src//moon/grid2d.clj
     312 src//moon/world_fns/modules.clj
     318 src//moon/grid.clj
     340 src//moon/info.clj
     375 src//moon/effect.clj
     405 src//moon/application/create/impl_txs.clj
     540 src//moon/schema.clj
    9143 total

</details>

<details>

<summary>
lein hiera :layout :horizontal
</summary>

<img width="5207" height="11784" alt="namespaces" src="https://github.com/user-attachments/assets/d6ea32d5-2290-4dd1-a82b-98a36d4408ea" />


</details>

