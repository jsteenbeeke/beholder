/*
 * This file is part of Beholder
 * (C) 2017 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function resolveOffsetX(expectedComponentId, event) {
  var x = event.offsetX;

  var eventTarget = $(event.target);

  while (eventTarget.attr('id') !== expectedComponentId) {
    x = x + eventTarget.position().left;

    eventTarget = eventTarget.parent();

    if (eventTarget === undefined) {
      return false;
    }
  }

  return Math.round(x);
}

function resolveOffsetY(expectedComponentId, event) {
  var y = event.offsetY;

  var eventTarget = $(event.target);

  while (eventTarget.attr('id') !== expectedComponentId) {
    y = y + eventTarget.position().top;

    eventTarget = eventTarget.parent();

    if (eventTarget === undefined) {
      return false;
    }
  }

  return Math.round(y);
}

